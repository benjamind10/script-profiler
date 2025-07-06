import { ComponentMeta } from "@inductiveautomation/perspective-client";
import HelloWorld from "./components/HelloWorld";

// Register the component with Perspective
export default {
  components: [
    {
      componentClass: HelloWorld,
      definition: {
        type: "script-profiler.helloWorld",
        name: "Hello World",
        description: "A simple hello world component.",
        defaultSize: { width: 4, height: 2 },
        props: []
      }
    } as ComponentMeta
  ]
};
