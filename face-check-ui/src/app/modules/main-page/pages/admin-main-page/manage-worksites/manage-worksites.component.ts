import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {WorkSiteControllerService} from "../../../../../services/services/work-site-controller.service";
import {FindAllWorkSites$Params} from "../../../../../services/fn/work-site-controller/find-all-work-sites";
import {PageResponseWorkSiteResponse} from "../../../../../services/models/page-response-work-site-response";
import {WorkSiteResponse} from "../../../../../services/models/work-site-response";
import {CreateWorkSite$Params} from "../../../../../services/fn/work-site-controller/create-work-site";
import {WorkSiteRequest} from "../../../../../services/models/work-site-request";
import {LocalTime} from "../../../../../services/models/local-time";


@Component({
  selector: 'app-manage-worksites',
  templateUrl: './manage-worksites.component.html',
  styleUrl: './manage-worksites.component.scss'
})
export class ManageWorksitesComponent implements OnInit{

  userName: string ='';
  companyName: string='';
  loading: boolean = false;
  errorMessage: string  = '';

  page: number = 0;
  size: number = 10;

  worksites: Array<WorkSiteResponse> = [];
  totalElements: number = 0;
  totalPages: number = 0;



  address: string = ''
  allowedRadius: number = 0
  latitude: number = 0;
  longitude: number = 0;
  workDayEnd: LocalTime = {};
  workDayStart: LocalTime = {};
  workSiteName: string = '';


  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private workSiteService: WorkSiteControllerService
  ) {}

  ngOnInit() {
    if(!this.authService.isUserAuthenticated()){
      this.authService.logout();
      return;
    }

    const userRole = this.authService.getUserRole();
    if(userRole!== 'ADMIN'){
      let targetUrl = '/';
      if(userRole === 'USER'){
        targetUrl = '/main-page/user';
      }
      window.location.href = targetUrl;
      return;
    }
    this.loadUserFullName();
    this.loadCompanyName();
    this.loadAllWorksites();

  }

  logout(){
    this.authService.logout();
  }


   loadUserFullName(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if(response && response.fullName){
          this.userName = response.fullName;
        }
      },
      error => {
        console.error('Error loading user full name', error);
      }
    );
  }

   loadCompanyName() {
    this.userService.findWorkerCompanyName().subscribe(
      response => {
       if(response && response.companyName){
         this.companyName = response.companyName
       }
      },
        error => {
        console.error('Error loading company name', error);
        }
    );
  }

  loadAllWorksites(): void {
    this.loading = true;
    this.errorMessage = '';

    const params: FindAllWorkSites$Params = {
      page: this.page,
      size: this.size
    };

    this.workSiteService.findAllWorkSites(params).subscribe(
      (response: PageResponseWorkSiteResponse) => {
        this.worksites = response.content || [];
        this.totalElements = response.totalElement || 0;
        this.totalPages = response.totalPages|| 0;
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Error loading worksites: ' + (error.message || 'Unknown Error');
        this.loading = false;
        console.error ('Error loading worksites:', error);
      }
    );
  }

  createNewWorkSite(){
  this.loading = true;
  this.errorMessage = '';
  const data: WorkSiteRequest = {
    address: this.address,
    allowedRadius: this.allowedRadius,
    latitude: this.latitude,
    longitude: this.longitude,
    workDayEnd: this.workDayEnd,
    workDayStart: this.workDayStart,
    workSiteName: this.workSiteName
  }

      const params: CreateWorkSite$Params = {
          body: data
      };
        this.workSiteService.createWorkSite(params).subscribe(
          (response: WorkSiteResponse) => {
            console.log('Worksite was created successfully:', response);
            this.loading = false;
            this.resetForm();
            this.loadAllWorksites();
          },
          error => {
            this.errorMessage = 'Cannot create worksite: ' + (error.message || 'Unknown error');
            this.loading = false;
            console.error('Cannot create worksite try later!', error);
          }
        )
  }

  private resetForm() {
    this.address = ''
    this.allowedRadius = 0
    this.latitude = 0;
    this.longitude = 0;
    this.workDayEnd = {};
    this.workDayStart = {};
    this.workSiteName= '';
  }
}
