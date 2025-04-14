import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {FileControllerService} from "../../../services/services/file-controller.service";
import {AuthService} from "./auth-service";
import {UserServiceControllerService} from "../../../services/services/user-service-controller.service";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
 export class PhotoService {


  private fileService: FileControllerService;
  private userService : UserServiceControllerService;
  private http: HttpClient;


  constructor(
    http: HttpClient,
    fileService: FileControllerService,
    userService: UserServiceControllerService) {
    this.http = http;
   this.fileService = fileService;
    this.userService = userService;
  }



  getUserPhoto(userPhotoUrl: string = ''): void {
    this.userService.findWorkerFullContactInformation().subscribe(
      response => {
        if (response && response.photoUrl) {
          userPhotoUrl = response.photoUrl;
        }
      },
      error => {
        console.error('Error loading user photo:', error);
      }
    );
  }


}
