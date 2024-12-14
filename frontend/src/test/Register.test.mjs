import { render, screen, fireEvent } from '@testing-library/react';
import Register from '../pages/Register';
import { register } from '../services/AuthService';
import {expect, test} from "@jest/globals";
jest.mock('../services/AuthService', () => ({
    register: jest.fn(),
}));

test('renders Register component and submits form', async () => {
    register.mockResolvedValueOnce({ message: 'Registration successful' });

    render(<Register />);

    const usernameInput = screen.getByPlaceholderText(/Username/i);
    const emailInput = screen.getByPlaceholderText(/Email/i);
    const passwordInput = screen.getByPlaceholderText(/Password/i);
    const button = screen.getByText(/Register/i);

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(button);

    expect(register).toHaveBeenCalledWith('testuser', 'test@example.com', 'password123');
    await screen.findByText(/Registration successful/i);
});
