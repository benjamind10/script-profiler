import React from "react";

export default function HelloWorld() {
  return (
    <div className="flex items-center justify-center h-full w-full">
      <div className="text-center bg-white shadow-lg rounded-2xl p-8">
        <h1 className="text-3xl font-bold mb-2">Hello, Perspective!</h1>
        <p className="text-lg text-gray-600">This is your first React-based Perspective page.</p>
      </div>
    </div>
  );
}
