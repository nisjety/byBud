import { Link } from 'react-router-dom';

const Navbar = () => (
  <nav>
    <ul>
      <li><Link to="/">Home</Link></li>
      <li><Link to="/login">Login</Link></li>
      <li><Link to="/register">Register</Link></li>
      <li><Link to="/deliveries">Deliveries</Link></li>
      <li><Link to="/deliveries/create">Create Delivery</Link></li>
    </ul>
  </nav>
);

export default Navbar;