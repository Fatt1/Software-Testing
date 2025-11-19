const presets = [
  ['@babel/preset-env', { targets: { node: 'current' } }],
  ['@babel/preset-react', { runtime: 'automatic' }],
];

const plugins = [
  ['@babel/plugin-syntax-import-meta'],
];

module.exports = { presets, plugins };
