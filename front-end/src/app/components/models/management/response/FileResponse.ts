import { FileHeaderDto } from "../FileHeaderDto";
import { File_cDto } from "../File_cDto";
import { PathFile } from "./PathFile";
export class FileResponse {
  pathFiles: PathFile[];
  file: File_cDto;
}
