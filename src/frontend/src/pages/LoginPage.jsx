import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../components/AuthContext.jsx';
import ErrorMessage from '../components/ErrorMessage.jsx';
import FormField from '../components/FormField.jsx';

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// 12-ux-ui/screens/login.md — HU-02.
export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const validate = () => {
    const next = {};
    if (!EMAIL_PATTERN.test(form.email.trim())) next.email = 'Enter a valid email address.';
    if (!form.password) next.password = 'Enter your password.';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setApiError('');
    if (!validate()) return;
    setSubmitting(true);
    try {
      await login(form.email.trim(), form.password);
      navigate('/tasks');
    } catch (error) {
      setApiError(error.status === 401 ? 'Invalid email or password.' : error.message);
    } finally {
      setSubmitting(false);
    }
  };

  const update = (field) => (event) => setForm({ ...form, [field]: event.target.value });

  return (
    <section className="card auth-card">
      <h1>Log in</h1>
      <ErrorMessage message={apiError} />
      <form onSubmit={handleSubmit} noValidate>
        <FormField id="email" label="Email" type="email" autoComplete="email"
          value={form.email} onChange={update('email')} error={errors.email} />
        <FormField id="password" label="Password" type="password" autoComplete="current-password"
          value={form.password} onChange={update('password')} error={errors.password} />
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Logging in…' : 'Log in'}
        </button>
      </form>
      <p>
        <Link to="/register">Create an account</Link>
      </p>
    </section>
  );
}
