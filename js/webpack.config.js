module.exports = {
  entry: "./index.ts",
  output: {
    filename: "script-profiler.js",
    path: path.resolve(__dirname, "../resources/js/"),
    libraryTarget: "amd"
  },
  // include loaders for TS, JSX, etc.
};
