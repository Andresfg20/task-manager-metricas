import NavBar from './components/NavBar.jsx';
import AppRoutes from './routes/AppRoutes.jsx';

export default function App() {
  return (
    <>
      <NavBar />
      <main className="container">
        <AppRoutes />
      </main>
    </>
  );
}
