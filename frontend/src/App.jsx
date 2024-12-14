import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import HomePage from './pages/HomePage';
import Login from './pages/Login';
import Register from './pages/Register';
import DeliveryList from './pages/DeliveryList';
import CreateDelivery from './pages/CreateDelivery';
import BidList from './pages/BidList';
import Navbar from './components/Navbar';
import './App.css';

const App = () => (
  <Router>
    <div className="App">
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/delivery" element={<DeliveryList />} />
          <Route path="/delivery/create" element={<CreateDelivery />} />
          <Route path="/bids/:deliveryId" element={<BidList />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  </Router>
);

export default App;