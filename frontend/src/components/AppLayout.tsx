import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';

export default function AppLayout() {
  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Navbar />
      <main style={{ flex: 1, maxWidth: 1200, margin: '0 auto', width: '100%', padding: '24px' }}>
        <Outlet />
      </main>
    </div>
  );
}
