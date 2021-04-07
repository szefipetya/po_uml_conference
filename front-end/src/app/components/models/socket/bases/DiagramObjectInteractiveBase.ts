import { Input, TemplateRef } from '@angular/core';
import { CommonService } from '../../../full-page-components/editor/services/common/common.service';
import { EditorSocketControllerService } from '../../../full-page-components/editor/services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../../full-page-components/editor/services/global-editor/global-editor.service';
import { soft_copy } from '../../../utils/utils';
import { DiagramObject } from '../../DiagramObjects/DiagramObject';
import { SimpleClass_General } from '../../DiagramObjects/SimpleClass_General';

import { ACTION_TYPE } from '../ACTION_TYPE';
import { EditorAction } from '../EditorAction';
import { CallbackItem } from '../interface/CallbackItem';
import { InteractiveItemBase } from './InteractiveItemBase';
