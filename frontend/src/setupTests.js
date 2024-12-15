import '@testing-library/jest-dom';
import { TextEncoder, TextDecoder } from 'util';


// Add global polyfills for TextEncoder and TextDecoder
if (typeof globalThis === 'object') {
    globalThis.TextEncoder = TextEncoder;
    globalThis.TextDecoder = TextDecoder;
} else if (typeof window === 'object') {
    window.TextEncoder = TextEncoder;
    window.TextDecoder = TextDecoder;
}

