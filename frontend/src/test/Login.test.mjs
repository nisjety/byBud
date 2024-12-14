import { render, screen, fireEvent } from '@testing-library/react';
import Login from '../pages/Login.jsx';
import { test, expect } from '@jest/globals';

test('renders Login component and submits form', () => {
    render(<Login />);

    const input = screen.getByPlaceholderText(/Username or Email/i);
    fireEvent.change(input, { target: { value: 'testuser' } });

    const button = screen.getByText(/Login/i);
    expect(button).toBeInTheDocument();
    fireEvent.click(button);
});
