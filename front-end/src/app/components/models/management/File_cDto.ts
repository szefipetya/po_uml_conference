import { FolderDto } from "./FolderDto";
import { FileClientModel } from "./FileClientModel";
import { User } from "../User";
import { ICON } from "./ICON";
import { FileComponent } from "../../left-panel-component/file/file.component";
export class File_cDto {
  viewModel: FileComponent;
  id: number;
  name: string;
  parentFolder_id: number;
  owner: User;
  icon: ICON;
  _type: string;
  clientModel: FileClientModel = new FileClientModel();
  constructor() {
    this.clientModel = new FileClientModel();
  }
}
