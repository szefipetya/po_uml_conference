import { EditorAction } from '../EditorAction';
import { RESPONSE_SCOPE } from './RESPONSE_SCOPE';
import { CustomResponse } from './CustomResponse';

export class EditorActionResponse extends CustomResponse {
  scope: RESPONSE_SCOPE;
  action: EditorAction;
}
