// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.









export const environment = {
  mockTestMode: false,
  production: false,
  api_url_http: 'http://localhost:8101/',
  api_url_raw: 'localhost:8101/', testdg_id: '5'
};
export enum endP {
  management = 'management/',
  project_management = 'project_management/',
  project = 'project/',
  create_project = 'create_project/',
  user_root_folder = 'user_root_folder/', create_folder = 'create_folder/', folder = 'folder/',
  share = 'share/'
}

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
