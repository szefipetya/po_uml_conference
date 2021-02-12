import { Canvas } from "./canvas";
import { SimpleClass_General } from "./SimpleClass_General";
import { SimpleClass } from "./SimpleClass";
import { SimpleClass_Scaled } from "./SimpleClass_Scaled";
import { User } from "./User";

export class Model{
  user:User;

    toolbox: {
      width: 120,
    };
    menubar: {
      height: 40,
    };
    canvas: Canvas;
    clip: {
      width: 1000,
      height: 550,
    };
    class_general: SimpleClass_General;
    selectedClass: null;
    classes: SimpleClass[];


};
