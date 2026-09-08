import HomePage from './app/pages/HomePage';
import MadisonScPage from './projects/madisonsc/pages/MadisonScPage';
import NavBar from './app/components/NavBar';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

function NotFoundPage() {
  return (
    <main>
      <h1>Page not found</h1>
      <p>The page you requested does not exist.</p>
    </main>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <NavBar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/madisonsc" element={<MadisonScPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}
