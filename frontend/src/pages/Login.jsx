import {useState} from 'react';
import {login} from '../services/AuthService';

const API_ENDPOINT = '/api/auth/login';
const REDIRECT_URL = '/delivery';

import PropTypes from 'prop-types';

const InputField = ({type, placeholder, value, onChange, required}) => (
    <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        required={required}
    />
);

InputField.propTypes = {
    type: PropTypes.string.isRequired,
    placeholder: PropTypes.string,
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
    required: PropTypes.bool
};

const Login = () => {
    const [identifier, setIdentifier] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const data = await login(identifier, password, API_ENDPOINT);
            localStorage.setItem('token', data.accessToken);
            alert('Login successful!');
            window.location.href = REDIRECT_URL;
        } catch (error) {
            const errorMessage = error.response?.data?.message || error.message;
            alert(`Login failed: ${errorMessage}. ${errorMessage === 'Network Error' ? 'Please check your internet connection.' : ''}`);
        }
    };

    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <InputField
                    type="text"
                    placeholder="Username or Email"
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                    required
                />
                <InputField
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

export default Login;
