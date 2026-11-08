import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../components/AuthContext.jsx';
import ErrorMessage from '../components/ErrorMessage.jsx';
import FormField from '../components/FormField.jsx';

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d).{8,72}$/;
const PASSWORD_RULE = 'Use 8 to 72 characters with at least one letter and one number.';

// 12-ux-ui/screens/register.md — HU-01. Every new account is USER (BR-62).
export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '', confirm: '' });
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const validate = () => {
    const next = {};
    if (!EMAIL_PATTERN.test(form.email.trim())) next.email = 'Enter a valid email address.';
    if (!PASSWORD_PATTERN.test(form.password)) next.password = PASSWORD_RULE;
    if (form.confirm !== form.password) next.confirm = 'The passwords do not match.';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setApiError('');
    if (!validate()) return;
    setSubmitting(true);
    try {
      await register(form.email.trim(), form.password);
      navigate('/tasks');
    } catch (error) {
      if (error.status === 409) {
        setErrors({ email: 'This email is already registered.' });
      } else if (error.details?.length) {
        setErrors(Object.fromEntries(error.details.map((d) => [d.field, d.message])));
      } else {
        setApiError(error.message);
      }
    } finally {
      setSubmitting(false);
    }
  };

  const update = (field) => (event) => setForm({ ...form, [field]: event.target.value });

  return (
    <section className="card auth-card">
      <h1>Create an account</h1>
      <ErrorMessage message={apiError} />
      <form onSubmit={handleSubmit} noValidate>
        <FormField id="email" label="Email" type="email" autoComplete="email" maxLength={254}
          value={form.email} onChange={update('email')} error={errors.email} />
        <FormField id="password" label="Password" type="password" autoComplete="new-password"
          hint={PASSWORD_RULE} value={form.password} onChange={update('password')} error={errors.password} />
        <FormField id="confirm" label="Confirm password" type="password" autoComplete="new-password"
          value={form.confirm} onChange={update('confirm')} error={errors.confirm} />
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Creating…' : 'Create account'}
        </button>
      </form>
      <p>
        <Link to="/login">I already have an account</Link>
      </p>
    </section>
  );
}
