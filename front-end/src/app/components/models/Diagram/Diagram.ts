import { Canvas } from './canvas';
import { SimpleClass_General } from '../DiagramObjects/SimpleClass_General';
import { SimpleClass } from '../DiagramObjects/SimpleClass';
import { DiagramObject_Scaled } from '../DiagramObjects/DiagramObject_Scaled';
import { User } from '../User';
import { LineCanvas } from '../line/LineCanvas';
import { Clip } from './Clip';
import { ClientModel } from './ClientModel';
import { Line } from '../line/Line';

export class Diagram {
  owner: User;
  classes: SimpleClass[];
  lines: Line[];
  // clientModel: ClientModel;
}
