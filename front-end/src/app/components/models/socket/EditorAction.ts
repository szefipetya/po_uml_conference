import { ActionTarget } from './ActionTarget';
import { ACTION_TYPE } from './ACTION_TYPE';

export class EditorAction {
  constructor(id, type, parent_id) {
    this.id = new Date().getMilliseconds().toString();
    this.target = new ActionTarget(id, type, parent_id);
  }
  session_jwt: string;
  user_id: string;
  id: string;
  json: string;
  action: ACTION_TYPE;
  target: ActionTarget;
  extra: any;
}
