import { DiagramObject } from "./DiagramObject";
import { PackageElement } from "./PackageElement";
import { TitleElement } from "./TitleElement";
export class PackageObject extends DiagramObject {
  titleModel: TitleElement;
  elements: PackageElement[];
}
