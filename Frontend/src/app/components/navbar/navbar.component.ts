import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css',
  ]
})
export class NavbarComponent implements OnInit {
  showNavbar: boolean = true;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Subscribe to route changes
    this.router.events.subscribe(() => {
      const currentRoute = this.router.url;
      // Hide navbar for login and register pages
      this.showNavbar = !(currentRoute.includes('login') || currentRoute.includes('register'));
    });
  }
}

