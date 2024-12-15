const JSDOMEnvironment = require('jest-environment-jsdom').default;

class CustomJestEnvironment extends JSDOMEnvironment {
    async setup() {
        await super.setup();
        if (typeof this.global.TextEncoder === 'undefined') {
            const { TextEncoder, TextDecoder } = require('util');
            this.global.TextEncoder = TextEncoder;
            this.global.TextDecoder = TextDecoder;
        }
    }
}

module.exports = CustomJestEnvironment;
