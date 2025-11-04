## Preview: Service Center Card UI

### Example 1: EVCare HCM Center

```
┌─────────────────────────────────────────────┐
│  [Image: EV charging station]               │
│  🟢 Open           ⭐ 4.5                   │
│                                             │
│─────────────────────────────────────────────│
│ EVCare HCM Center                          │
│ Trung tâm bảo dưỡng EV tại Quận 1, TP.HCM │
│─────────────────────────────────────────────│
│ 📍 123 Lê Lợi, Bến Thành, Quận 1,         │
│    Hồ Chí Minh                             │
│ 🧭 2.8 km away                             │
│ 🕐 07:00 - 23:15 (Open)                    │
│ 📞 02837651234        👥 3 Staff           │
└─────────────────────────────────────────────┘
```

### Example 2: EVCare HCM Tân Bình

```
┌─────────────────────────────────────────────┐
│  [Image: EV service center]                 │
│  🟢 Open           ⭐ N/A                   │
│                                             │
│─────────────────────────────────────────────│
│ EVCare HCM Tân Bình                        │
│ Trung tâm bảo dưỡng EV tại Quận Tân Bình  │
│─────────────────────────────────────────────│
│ 📍 210 Cộng Hòa, 4, Tân Bình,             │
│    Hồ Chí Minh                             │
│ 🧭 4.2 km away                             │
│ 🕐 07:00 - 21:00 (Open)                    │
│ 📞 02837651234        👥 3 Staff           │
└─────────────────────────────────────────────┘
```

## Color Scheme

### Status Colors

- 🟢 **Open**: Green (#4CAF50)
- 🔴 **Closed**: Red (#F44336)

### UI Elements

- **Card Background**: White (#FFFFFF)
- **Card Border**: Light Gray (#E0E0E0)
- **Primary Text**: Dark Gray (#212121)
- **Secondary Text**: Gray (#757575)
- **Icon Colors**:
  - Location: Blue (#2196F3)
  - Distance: Orange (#FF9800)
  - Time: Green (#4CAF50)
  - Phone: Teal (#009688)
  - Staff: Purple (#9C27B0)
  - Star: Amber (#FFC107)

## Card Layout Breakdown

```
┌───────────────────────────────┐
│ ┌───────────────────────────┐ │ ← FrameLayout (Image Container)
│ │                           │ │
│ │   Service Center Image    │ │   180dp height
│ │                           │ │
│ │  [Gradient Overlay]       │ │
│ │  🟢 Status    ⭐ Rating  │ │ ← Badges overlay
│ └───────────────────────────┘ │
│                               │
│ ┌───────────────────────────┐ │ ← Content Section
│ │ Title (18sp, Bold)        │ │   16dp padding
│ │ Description (13sp)        │ │
│ │ ─────────────────────     │ │ ← Divider
│ │ 📍 Address                │ │
│ │ 🧭 Distance               │ │
│ │ 🕐 Operating Hours        │ │
│ │ 📞 Phone      👥 Staff   │ │
│ └───────────────────────────┘ │
└───────────────────────────────┘
```

## API Field Mapping

| API Field              | UI Element     | Format        |
| ---------------------- | -------------- | ------------- |
| `name`                 | Title          | Plain text    |
| `description`          | Subtitle       | Max 2 lines   |
| `address.street`       | Address line 1 | Concatenated  |
| `address.ward`         | Address line 2 | With commas   |
| `address.district`     | Address line 3 |               |
| `address.city`         | Address line 4 |               |
| `contact.phone`        | Phone text     | Plain         |
| `rating.average`       | Rating number  | 1 decimal     |
| `operatingHours.{day}` | Hours text     | HH:mm - HH:mm |
| `staff.length`         | Staff count    | "X Staff"     |
| `images[0].url`        | Card image     | Glide load    |
| `distance`             | Distance text  | "X km away"   |
| `status`               | Status badge   | Open/Closed   |

## Interaction States

### Normal State

- Card elevation: 4dp
- Border: 1dp solid #E0E0E0

### Pressed State (Ripple Effect)

- Material ripple animation
- Slightly darker overlay

### Scrolling

- Smooth RecyclerView scroll
- Item animations on appear

## Accessibility

- **Text Contrast**: All text meets WCAG AA standards
- **Touch Targets**: Minimum 48dp for clickable areas
- **Content Description**: All icons have proper descriptions
- **Screen Reader**: All content readable by TalkBack

## Responsive Behavior

### Small Screens (<360dp width)

- Same layout, adjusts padding
- Text may wrap more

### Medium Screens (360-600dp width)

- Default layout
- Optimal viewing

### Large Screens (>600dp width)

- Can use GridLayoutManager with 2 columns
- More cards visible at once
