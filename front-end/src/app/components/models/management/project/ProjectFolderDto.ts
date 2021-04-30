import { File_cDto } from "../File_cDto";
import { ProjectFileDto } from "./ProjectFileDto";
import { ProjectFileHeaderDto } from "./ProjectFileHeaderDto";
export class ProjectFolderDto extends ProjectFileDto {
  files: ProjectFileHeaderDto[];
  is_projectRoot: boolean;
  relatedDiagramId: number;


}
