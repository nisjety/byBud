export default {
    setupFilesAfterEnv: ['<rootDir>/src/setupTests.js'],
    transform: {
        '^.+\\.[tj]sx?$': ['babel-jest', { presets: ['@babel/preset-env', '@babel/preset-react'] }],
    },
    extensionsToTreatAsEsm: ['.jsx'],
    testEnvironment: '<rootDir>/customJestEnvironment.js',
    moduleNameMapper: {
        '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
    },
    testMatch: ['**/?(*.)+(spec|test).[tj]s?(x)'],
};
