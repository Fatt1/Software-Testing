/**
 * @see https://github.com/testing-library/jest-dom
 */

// Import custom Jest matchers for DOM element assertions
require("@testing-library/jest-dom");


const { TextEncoder, TextDecoder } = require('util');

global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;
