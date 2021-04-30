import { FileHeaderDto } from "../FileHeaderDto";
import { File_cDto } from "../File_cDto";
import { ProjectFileHeaderDto } from "./ProjectFileHeaderDto";
export class ProjectFolderHeaderDto extends ProjectFileHeaderDto {
  is_projectRoot: boolean;
  relatedDiagramId: number;

}
