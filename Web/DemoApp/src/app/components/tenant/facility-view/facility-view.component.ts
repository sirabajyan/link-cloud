import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {IFacilityConfigModel} from "../../../interfaces/tenant/facility-config-model.interface";
import {TenantService} from "../../../services/gateway/tenant/tenant.service";
import {ActivatedRoute} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';
import {RouterLink} from '@angular/router';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatTableModule} from '@angular/material/table';
import {MatCardModule} from '@angular/material/card';
import {createFakeData, Report} from "./fake-data";

@Component({
  selector: 'app-facility-view',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    MatTooltipModule,
    MatTableModule,
    MatCardModule,
    RouterLink
  ],
  templateUrl: './facility-view.component.html',
  styleUrls: ['./facility-view.component.scss']
})
export class FacilityViewComponent {
  public facilityId: string = '';
  public facilityConfig!: IFacilityConfigModel;
  public reports: Report[] = createFakeData();

  constructor(private tenantService: TenantService, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.facilityId = params['id'];
      this.loadFacilityConfig();
    });
  }

  loadFacilityConfig(): void {
    this.tenantService.getFacilityConfiguration(this.facilityId).subscribe((data: IFacilityConfigModel) => {
      this.facilityConfig = data;
    });
  }
}
