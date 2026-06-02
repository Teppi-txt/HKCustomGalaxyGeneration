"use strict";

const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");

const srcDir = path.resolve(__dirname, 'src');

module.exports = {
  mode: "development",
  entry: {
    main: "./src/js/main.ts",
    credits: "./src/js/main-credits.js",
  },
  output: {
    filename: "[name].bundle.js",
    path: path.resolve(__dirname, "dist"),
  },
  devServer: {
    static: path.resolve(__dirname, "dist"),
    port: 8080,
    hot: true,
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: "./src/index.html",
      favicon: `${srcDir}/favicon.ico`,
      chunks: ["main"]
    }),
    new HtmlWebpackPlugin({
      template: "./src/credits.html",
      favicon: `${srcDir}/favicon.ico`,
      filename: "credits.html",
      chunks: ["credits"],
    }),
  ],
  module: {
    rules: [
      {
        test: /\.html$/,
        loader: 'html-loader'
      },
      {
        test: /\.tsx?$/,
        use: "ts-loader",
        exclude: /node_modules/,
      },
    ],
  },
  resolve: {
    extensions: [".ts", ".js"],
  },
};
