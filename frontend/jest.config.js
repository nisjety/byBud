/** @type {import('jest').Config} */
export default {
    setupFilesAfterEnv: ['<rootDir>/src/setupTests.js'], // Include the setup file
    transform: {
        '^.+\\.[tj]sx?$': 'babel-jest', // Use Babel for transforming JS/TSX files
    },
    testEnvironment: 'jsdom', // Simulate browser-like environment
    moduleNameMapper: {
        '\\.(css|less|scss|sass)$': 'identity-obj-proxy', // Mock CSS imports
    },
    testMatch: [
        '**/?(*.)+(spec|test).[tj]s?(x)', // Look for test and spec files
        '**/?(*.)+(spec|test).mjs',      // Include .mjs test files explicitly
    ],
};
