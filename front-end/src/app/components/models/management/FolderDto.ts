import { FileHeaderDto } from "./FileHeaderDto";
import { File_cDto } from "./File_cDto";

export class FolderDto extends File_cDto {
  files: FileHeaderDto[];
  is_root: boolean;
}
