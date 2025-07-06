const path = require("path");

module.exports = {
  entry: "./src/GatewayPage.tsx",
  output: {
    filename: "script-profiler.js",
    path: path.resolve(__dirname, "../gateway/src/main/resources/web/profiler"),
    libraryTarget: "umd",
    globalObject: "this",
  },
  resolve: {
    extensions: [".ts", ".tsx", ".js"],
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        loader: "ts-loader",
        options: { transpileOnly: true },
      },
    ],
  },
};
