import { Component, OnInit, OnDestroy, AfterViewInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { AuthService } from '../../../additionalServices/auth-service';
import {
  CompanyControllerService,
  UserServiceControllerService,
  TrackLocationControllerService
} from '../../../../../services/services';
import { RelatedUserInCompanyResponse } from '../../../../../services/models/related-user-in-company-response';
import { LocationRecordDto } from '../../../../../services/models/location-record-dto';
import { GetAllEmployees$Params } from '../../../../../services/fn/company-controller/get-all-employees';
import { GetLocationHistory$Params } from '../../../../../services/fn/track-location-controller/get-location-history';
import {UserDataService} from "../../../../components/user-data-service/user-data-service";
import {Subscription} from "rxjs";

// Импортируем Leaflet
declare let L: any;

// Интерфейс для локации
interface LocationData {
  latitude: number;
  longitude: number;
  timestamp: string;
  accuracy: number;
  speed: number;
  batteryLevel: number;
  distanceFromPrevious: number;
}

@Component({
  selector: 'app-loc-track',
  templateUrl: './loc-track.component.html',
  styleUrls: ['./loc-track.component.scss']
})
export class LocTrackComponent implements OnInit, OnDestroy, AfterViewInit {

  // User info
  public userName: string = '';
  public companyName: string = '';
  public userPhotoUrl: string = '';

  // Employees
  public employees: RelatedUserInCompanyResponse[] = [];
  public filteredEmployees: RelatedUserInCompanyResponse[] = [];
  public selectedEmployee: RelatedUserInCompanyResponse | null = null;
  public searchTerm: string = '';

  public page: number = 0;
  public size: number = 10;
  public totalPages: number = 0;
  public totalElements: number = 0;

  // Location data
  public locations: LocationData[] = [];
  public currentLocationIndex: number = 0;

  // Map
  private map: any = null;
  private markersLayer: any = null;
  private routePolyline: any = null;
  private currentMarker: any = null;
  private mapInitialized: boolean = false;

  // Playback
  public isPlaying: boolean = false;
  public playbackSpeed: number = 1;
  private playbackInterval: any = null;

  // UI State
  public loading: boolean = false;
  public loadingEmployees: boolean = false;
  public errorMessage: string = '';
  public successMessage: string = '';
  public selectedDate: string;
  public maxDate: string;

  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private locationService: TrackLocationControllerService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    public userDataService: UserDataService

  ) {
    // Устанавливаем текущую дату
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    this.selectedDate = `${year}-${month}-${day}`;
    this.maxDate = `${year}-${month}-${day}`;
  }

  public ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const userRole = this.authService.getUserRole();
    if (userRole !== 'ADMIN' && userRole !== 'FOREMAN') {
      window.location.href = '/main-page/user';
      return;
    }

    this.subscriptions.add(
      this.userDataService.userName$.subscribe(name => {
        if (name) this.userName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.companyName$.subscribe(name => {
        if (name) this.companyName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.userPhoto$.subscribe(photo => {
        if (photo) this.userPhotoUrl = photo;
      })
    );

       this.loadEmployees();
  }

  public ngAfterViewInit(): void {
    // Инициализация карты вне Angular зоны для производительности
    this.ngZone.runOutsideAngular(() => {
      setTimeout(() => {
        this.initializeMap();
      }, 500);
    });
  }

  public ngOnDestroy(): void {
    this.stopPlayback();
    if (this.map) {
      this.map.remove();
      this.map = null;
      this.mapInitialized = false;
    }
    this.subscriptions.unsubscribe();

  }

  private initializeMap(): void {
    // Проверяем наличие Leaflet
    if (typeof L === 'undefined') {
      console.error('Leaflet is not loaded!');
      this.loadLeafletFromCDN();
      return;
    }

    const mapElement = document.getElementById('locationMap');

    if (!mapElement) {
      console.error('Map element not found, retrying...');
      setTimeout(() => this.initializeMap(), 1000);
      return;
    }

    // Удаляем старую карту если есть
    if (this.map) {
      this.map.remove();
      this.map = null;
    }

    // Очищаем контейнер
    mapElement.innerHTML = '';

    try {
      // Устанавливаем размеры явно
      mapElement.style.width = '100%';
      mapElement.style.height = '500px';
      mapElement.style.minHeight = '500px';
      mapElement.style.display = 'block';
      mapElement.style.position = 'relative';
      mapElement.style.zIndex = '1';

      // Создаем карту
      this.map = L.map(mapElement, {
        center: [40.7128, -74.0060],
        zoom: 13,
        zoomControl: true,
        attributionControl: true,
        preferCanvas: false,
        renderer: L.svg()
      });

      // Добавляем тайлы
      L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '© OpenStreetMap contributors © CARTO',
        subdomains: 'abcd',
        maxZoom: 20,
        minZoom: 2
      }).addTo(this.map);

      // Создаем группу для маркеров
      this.markersLayer = L.layerGroup().addTo(this.map);

      // Обновляем размер карты
      setTimeout(() => {
        if (this.map) {
          this.map.invalidateSize(true);
        }
      }, 100);

      setTimeout(() => {
        if (this.map) {
          this.map.invalidateSize(true);
        }
      }, 500);

      setTimeout(() => {
        if (this.map) {
          this.map.invalidateSize(true);
          this.map.setView([40.7128, -74.0060], 13);
        }
      }, 1000);

      // Обработчик изменения размера окна
      window.addEventListener('resize', () => {
        if (this.map) {
          setTimeout(() => {
            this.map.invalidateSize(true);
          }, 200);
        }
      });

      this.mapInitialized = true;
      console.log('Map initialized successfully');

      // Обновляем Angular
      this.ngZone.run(() => {
        this.cdr.detectChanges();
      });

    } catch (error) {
      console.error('Error initializing map:', error);
      this.mapInitialized = false;
    }
  }

  private loadLeafletFromCDN(): void {
    // Загружаем CSS
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
    document.head.appendChild(link);

    // Загружаем JS
    const script = document.createElement('script');
    script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
    script.onload = () => {
      console.log('Leaflet loaded from CDN');
      setTimeout(() => this.initializeMap(), 500);
    };
    document.head.appendChild(script);
  }


  public async loadEmployees(): Promise<void> {
    this.loadingEmployees = true;
    this.errorMessage = '';

    try {
      const companyId = await this.userService.findWorkerCompanyIdByAuthentication().toPromise();

      if (!companyId?.companyId) {
        throw new Error('Company ID not found');
      }

      const params: GetAllEmployees$Params = {
        companyId: companyId.companyId,
        page: this.page,           // ИЗМЕНЕНО
        size: this.size            // ИЗМЕНЕНО
      };

      this.companyService.getAllEmployees(params).subscribe(
        response => {
          this.employees = response.content || [];
          this.totalPages = response.totalPages || 0;      // ДОБАВЛЕНО
          this.totalElements = response.totalElement || 0;  // ДОБАВЛЕНО
          this.filterEmployees();
          this.loadingEmployees = false;
        },
        error => {
          this.errorMessage = 'Error loading employees';
          this.loadingEmployees = false;
        }
      );
    } catch (error) {
      this.errorMessage = 'Error loading employees';
      this.loadingEmployees = false;
    }
  }

  public changePage(newPage: number): void {
    this.page = newPage;
    this.loadEmployees();
  }

  public filterEmployees(): void {
    if (!this.searchTerm.trim()) {
      this.filteredEmployees = [...this.employees];
    } else {
      const search = this.searchTerm.toLowerCase();
      this.filteredEmployees = this.employees.filter(emp =>
        emp.firstName?.toLowerCase().includes(search) ||
        emp.lastName?.toLowerCase().includes(search) ||
        emp.workerId?.toString().includes(search)
      );
    }
  }

  public selectEmployee(employee: RelatedUserInCompanyResponse): void {
    this.selectedEmployee = employee;
    this.locations = [];
    this.currentLocationIndex = 0;
    this.stopPlayback();

    // Переинициализируем карту если нужно
    if (!this.mapInitialized) {
      this.ngZone.runOutsideAngular(() => {
        this.initializeMap();
      });
    } else {
      // Обновляем размер карты
      setTimeout(() => {
        if (this.map) {
          this.map.invalidateSize(true);
        }
      }, 200);
    }

    if (employee.workerId) {
      this.loadLocationHistory();
    }
  }

  public loadLocationHistory(): void {
    if (!this.selectedEmployee?.workerId || !this.selectedDate) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.locations = [];
    this.clearMap();

    console.log('Loading history for user:', this.selectedEmployee.workerId);
    console.log('Selected date:', this.selectedDate);

    const params: GetLocationHistory$Params = {
      userId: this.selectedEmployee.workerId,
      startDate: this.selectedDate
    };

    this.locationService.getLocationHistory(params).subscribe(
      (response: LocationRecordDto[]) => {
        console.log('Received locations:', response);

        // Проверяем что это действительно массив
        if (!Array.isArray(response)) {
          console.error('Response is not an array:', response);
          this.errorMessage = 'Invalid response format';
          this.loading = false;
          return;
        }

        // Маппим LocationRecordDto в LocationData
        this.locations = response
          .filter(item =>
            item.latitude &&
            item.longitude &&
            item.latitude !== 0 &&
            item.longitude !== 0
          )
          .map(item => ({
            latitude: item.latitude!,
            longitude: item.longitude!,
            timestamp: item.timestamp || new Date().toISOString(),
            accuracy: item.accuracy || 10,
            speed: item.speed || 0,
            batteryLevel: item.batteryLevel || 100,
            distanceFromPrevious: item.distanceFromPrevious || 0
          }))
          .sort((a, b) =>
            new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
          );

        console.log(`Processed ${this.locations.length} locations`);

        this.loading = false;

        if (this.locations.length > 0) {
          this.currentLocationIndex = 0;

          // Инициализируем или обновляем карту
          if (!this.mapInitialized || !this.map) {
            console.log('Initializing map...');
            this.ngZone.runOutsideAngular(() => {
              setTimeout(() => {
                this.initializeMap();
                setTimeout(() => {
                  this.drawRoute();
                  this.updateMapView();
                }, 1000);
              }, 500);
            });
          } else {
            this.drawRoute();
            this.updateMapView();
          }

          // Показываем сообщение об успехе
          const startTime = this.formatTime(this.locations[0].timestamp);
          const endTime = this.formatTime(this.locations[this.locations.length - 1].timestamp);
          this.successMessage = `Loaded ${this.locations.length} location points (${startTime} - ${endTime})`;
          setTimeout(() => this.successMessage = '', 5000);
        } else {
          this.errorMessage = 'No location data found for selected date';
        }
      },
      error => {
        console.error('Error loading location history:', error);
        this.loading = false;
        this.errorMessage = `Failed to load location data: ${error.message || 'Unknown error'}`;
      }
    );
  }

  public drawRoute(): void {
    if (!this.map || this.locations.length === 0 || typeof L === 'undefined') return;

    this.clearMap();

    const latLngs = this.locations.map(loc => L.latLng(loc.latitude, loc.longitude));

    // Рисуем маршрут
    this.routePolyline = L.polyline(latLngs, {
      color: '#5B47E0',
      weight: 4,
      opacity: 0.7,
      smoothFactor: 1
    }).addTo(this.markersLayer);

    // Маркер начала
    if (this.locations.length > 0) {
      const startLoc = this.locations[0];
      const startIcon = L.divIcon({
        className: 'custom-div-icon',
        html: '<div style="background: #00D97E; color: white; border-radius: 50%; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; font-weight: bold; border: 2px solid white; box-shadow: 0 2px 6px rgba(0,0,0,0.3);">S</div>',
        iconSize: [30, 30],
        iconAnchor: [15, 15]
      });

      L.marker([startLoc.latitude, startLoc.longitude], { icon: startIcon })
        .bindPopup(`<b>Start</b><br>${this.formatDateTime(startLoc.timestamp)}`)
        .addTo(this.markersLayer);
    }

    // Маркер конца
    if (this.locations.length > 1) {
      const endLoc = this.locations[this.locations.length - 1];
      const endIcon = L.divIcon({
        className: 'custom-div-icon',
        html: '<div style="background: #FF5757; color: white; border-radius: 50%; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; font-weight: bold; border: 2px solid white; box-shadow: 0 2px 6px rgba(0,0,0,0.3);">E</div>',
        iconSize: [30, 30],
        iconAnchor: [15, 15]
      });

      L.marker([endLoc.latitude, endLoc.longitude], { icon: endIcon })
        .bindPopup(`<b>End</b><br>${this.formatDateTime(endLoc.timestamp)}`)
        .addTo(this.markersLayer);
    }

    this.updateCurrentMarker();
  }

  public updateCurrentMarker(): void {
    if (!this.map || !this.locations[this.currentLocationIndex] || typeof L === 'undefined') return;

    if (this.currentMarker) {
      this.currentMarker.remove();
    }

    const currentLoc = this.locations[this.currentLocationIndex];

    const currentIcon = L.divIcon({
      className: 'custom-div-icon',
      html: '<div style="background: #5B47E0; width: 20px; height: 20px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3);"></div>',
      iconSize: [20, 20],
      iconAnchor: [10, 10]
    });

    this.currentMarker = L.marker([currentLoc.latitude, currentLoc.longitude], { icon: currentIcon })
      .addTo(this.markersLayer);

    if (this.isPlaying) {
      this.map.panTo([currentLoc.latitude, currentLoc.longitude]);
    }
  }

  public updateMapView(): void {
    if (!this.map || this.locations.length === 0 || typeof L === 'undefined') return;

    const bounds = L.latLngBounds(
      this.locations.map(loc => L.latLng(loc.latitude, loc.longitude))
    );

    this.map.fitBounds(bounds, {
      padding: [50, 50],
      maxZoom: 16
    });

    // Обновляем размер после изменения bounds
    setTimeout(() => {
      if (this.map) {
        this.map.invalidateSize(true);
      }
    }, 200);
  }

  public clearMap(): void {
    if (this.markersLayer) {
      this.markersLayer.clearLayers();
    }
    this.routePolyline = null;
    this.currentMarker = null;
  }

  public togglePlayback(): void {
    if (this.isPlaying) {
      this.stopPlayback();
    } else {
      this.startPlayback();
    }
  }

  public startPlayback(): void {
    if (this.locations.length < 2) return;

    this.isPlaying = true;
    this.playbackInterval = setInterval(() => {
      if (this.currentLocationIndex < this.locations.length - 1) {
        this.currentLocationIndex++;
        this.updateCurrentMarker();
      } else {
        this.stopPlayback();
      }
    }, 1000 / this.playbackSpeed);
  }

  public stopPlayback(): void {
    this.isPlaying = false;
    if (this.playbackInterval) {
      clearInterval(this.playbackInterval);
      this.playbackInterval = null;
    }
  }

  public onTimelineChange(event: any): void {
    const index = parseInt(event.target.value, 10);
    this.currentLocationIndex = index;
    this.updateCurrentMarker();
  }

  public refreshData(): void {
    if (this.selectedEmployee) {
      this.loadLocationHistory();
    }
    this.loadEmployees();

    // Переинициализируем карту при refresh
    if (!this.mapInitialized || !this.map) {
      this.ngZone.runOutsideAngular(() => {
        this.initializeMap();
      });
    } else {
      setTimeout(() => {
        if (this.map) {
          this.map.invalidateSize(true);
        }
      }, 200);
    }
  }

  // Helper methods
  public getCurrentLocation(): LocationData | undefined {
    return this.locations[this.currentLocationIndex];
  }

  public getTimeRange(): string {
    if (this.locations.length === 0) return '';
    const start = this.formatTime(this.locations[0].timestamp);
    const end = this.formatTime(this.locations[this.locations.length - 1].timestamp);
    return `${start} - ${end}`;
  }

  public getStartTime(): string {
    return this.locations.length > 0 ? this.formatTime(this.locations[0].timestamp) : '';
  }

  public getEndTime(): string {
    return this.locations.length > 0
      ? this.formatTime(this.locations[this.locations.length - 1].timestamp)
      : '';
  }

  public getTotalDistance(): string {
    if (this.locations.length < 2) return '0';

    let totalDistance = 0;
    for (let i = 0; i < this.locations.length; i++) {
      if (this.locations[i].distanceFromPrevious) {
        totalDistance += this.locations[i].distanceFromPrevious;
      }
    }
    return (totalDistance / 1000).toFixed(2);
  }

  public formatDateTime(timestamp: string): string {
    const date = new Date(timestamp);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      timeZone: 'UTC'
    });
  }

  public formatTime(timestamp: string): string {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      timeZone: 'UTC'
    });
  }

  public handleImageError(event: any): void {
    event.target.style.display = 'none';
    const placeholder = event.target.parentElement?.querySelector('.avatar-placeholder');
    if (placeholder) {
      placeholder.style.display = 'flex';
    }
  }

  // Добавьте этот геттер в класс компонента
  public get currentLocation(): LocationData {
    return this.locations[this.currentLocationIndex] || {
      latitude: 0,
      longitude: 0,
      timestamp: new Date().toISOString(),
      accuracy: 0,
      speed: 0,
      batteryLevel: 0,
      distanceFromPrevious: 0
    };
  }

  public formatLastTime(timestamp: string): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now.getTime() - date.getTime();

    if (diff < 60000) return 'Just now';
    if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`;
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`;
    return date.toLocaleDateString('en-US', { timeZone: 'UTC' });
  }

}
