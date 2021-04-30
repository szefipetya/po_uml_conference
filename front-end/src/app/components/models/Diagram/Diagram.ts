import { Canvas } from './canvas';
import { SimpleClass_General } from '../DiagramObjects/SimpleClass_General';
import { SimpleClass } from '../DiagramObjects/SimpleClass';
import { DiagramObject_Scaled } from '../DiagramObjects/DiagramObject_Scaled';
import { User } from '../User';
import { LineCanvas } from '../line/LineCanvas';
import { Clip } from './Clip';
import { ClientModel } from './ClientModel';
import { Line } from '../line/Line';
import { DiagramObject } from '../DiagramObjects/DiagramObject';

export class Diagram {
  id: number;
  owner: User;
  dgObjects: Array<DiagramObject>;
  lines: Line[];
  // clientModel: ClientModel;
}
