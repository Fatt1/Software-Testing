const presets = [
  ['@babel/preset-env', { targets: { node: 'current' } }],
  ['@babel/preset-react', { runtime: 'automatic' }],
];

const plugins = [
  ['@babel/plugin-syntax-import-meta'],
];

// Add Istanbul plugin for coverage instrumentation
if (process.env.NODE_ENV === 'test') {
  plugins.push(['istanbul']);
}

module.exports = { presets, plugins };
