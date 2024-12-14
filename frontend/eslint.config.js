import js from '@eslint/js';
import globals from 'globals';
import react from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import jest from 'eslint-plugin-jest';

export default [
  // Ignore dist directory
  { ignores: ['dist'] },
  {
    // Configuration for all JS/JSX files
    files: ['**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: {
        ...globals.browser, // Browser globals
      },
      parserOptions: {
        ecmaVersion: 'latest',
        ecmaFeatures: { jsx: true },
        sourceType: 'module',
      },
    },
    settings: {
      react: { version: 'detect' }, // Automatically detect React version
    },
    plugins: {
      react,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      ...js.configs.recommended.rules,
      ...react.configs.recommended.rules,
      ...react.configs['jsx-runtime'].rules,
      ...reactHooks.configs.recommended.rules,
      'react/react-in-jsx-scope': 'off', // Disable React in scope rule for JSX
      'react/jsx-no-target-blank': 'off',
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
      'no-unused-vars': 'warn', // Downgrade no-unused-vars to warning
    },
  },
  {
    // Specific configuration for test files
    files: ['**/*.test.{js,jsx}'],
    languageOptions: {
      globals: {
        ...globals.jest, // Jest globals like `describe`, `test`, `expect`
      },
    },
    plugins: { jest },
    rules: {
      ...(jest.configs?.recommended?.rules || {}), // Safely access Jest's recommended rules
      'jest/no-disabled-tests': 'warn',
      'jest/no-focused-tests': 'error',
      'jest/no-identical-title': 'error',
      'jest/valid-expect': 'error',
    },
  },
];

