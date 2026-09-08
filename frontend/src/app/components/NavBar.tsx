import { Link } from 'react-router-dom';

export default function NavBar() {
  return (
    <nav aria-label="Main navigation">
      <Link to="/">Home</Link>
      <Link to="/madisonsc">Madison SC</Link>
    </nav>
  );
}
