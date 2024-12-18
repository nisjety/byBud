ByBud
Frontend Documentation

         This documentation provides a detailed overview of the frontend structure, implementation, and usage for the **Bybud** application.

         Technologies Used

         - React: JavaScript library for building user interfaces.
         - React Router: For routing and navigation.
         - Axios: HTTP client for API requests.
         - React Toastify: Notification library.
         - CSS: For styling components.
         - Vite: Build tool for faster development and optimized production builds.


         Project Structure

         src/
         |-- components/      # Reusable React components (Navbar, Loader, ProtectedRoute, etc.)
         |-- pages/           # Application pages (Login, Register, UserProfile, etc.)
         |-- services/        # API service handlers (AuthService, UserService, etc.)
         |-- App.jsx          # Main application component
         |-- index.jsx        # Application entry point
         |-- styles/          # Global and component-specific styles

         Core Functionalities

         Authentication Workflow
         1. Login
            - Endpoint: `POST /api/auth/login`
            - Stores `accessToken` and `userId` in `localStorage`.
            - Redirects user to the profile page after successful login.

         2. **Protected Routes**
            - Ensures authenticated access using `ProtectedRoute` component.
            - Validates JWT token and redirects to login if invalid or missing.
            - Ensures only authenticated users can access certain routes.
            - Validates JWT tokens stored in `localStorage`.

         3. Logout
            - Clears `localStorage` on logout and redirects to login.

         ### **Profile Management**
         - **UserProfile**
           - Endpoint: `GET /api/users/{id}`
           - Displays and edits user profile information.
           - Allows users to view and edit their profile details.
           - Changes are sent using `PUT /api/users/{id}`.

         ### **Navigation**
         - **Navbar**
           - Displays dynamic links based on authentication state.
           - Handles logout functionality.

         Key Components

         Navbar
         -Props:
           - `authenticated`: Boolean indicating authentication status.
           - `onLogout`: Callback function to handle logout.

         ### **ProtectedRoute**



         #### API Methods:
         - `GET /api/users/{id}`: Fetches user data.
         - `PUT /api/users/{id}`: Updates user data.



         API Integration

         AuthService.js
         Handles authentication-related API requests.

         Login:
         javascript
         export const login = async (identifier, password) => {
             const response = await AuthAPI.post('/auth/login', { identifier, password });
             return response.data;
         };


         UserService.js
         Handles user-related API requests.

         - Get User Profile:**
         javascript
         export const getUserProfile = async (userId) => {
             const response = await UserAPI.get(`/users/${userId}`);
             return response.data;
         };



         Error Handling
         - Global Interceptors** (via `Axios`):
           - Adds `Authorization` headers.
           - Handles `401 Unauthorized` errors with token refresh logic.
           - Logs errors using `console.error` and shows notifications using `React Toastify`.


         Styling
         -Global Styles:**
           - Located in `src/styles/`.
           - Includes common styles for buttons, inputs, and layout.

         Component-Specific Styles:**
           - Scoped styles written as CSS or SCSS files.


         Future Improvements**
         - Implement context or Redux for global state management.
         - Add unit tests for critical components.
         - Optimize API error handling with centralized logging.


         1. Start Development Server
            npm install
            npm run dev

         2. Build for Production**
            npm run build


         3. Preview Production Build
            npm run preview

