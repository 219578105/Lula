# Lula — Small Business App

Smart business tools for South African shops. Works offline, syncs when back online.

## Files

| File | Purpose |
|------|---------|
| `index.html` | Login page with Google Sign-In |
| `setup.html` | Business setup wizard (store type selection) |
| `dashboard.html` | Main dashboard (sales, products, customers) |
| `style.css` | All styles |

## How to deploy on GitHub Pages

1. Create a new GitHub repository (e.g. `lula-app`)
2. Upload all 4 files to the repository
3. Go to **Settings → Pages**
4. Under "Branch", select `main` and click **Save**
5. Your app will be live at: `https://yourusername.github.io/lula-app/`

## Setting up Google Sign-In

1. Go to [console.cloud.google.com](https://console.cloud.google.com/)
2. Create a new project
3. Go to **APIs & Services → Credentials**
4. Click **Create Credentials → OAuth 2.0 Client ID**
5. Application type: **Web application**
6. Add your GitHub Pages URL to **Authorised JavaScript origins**
   - e.g. `https://yourusername.github.io`
7. Copy the **Client ID**
8. Open `index.html` and replace both instances of `YOUR_GOOGLE_CLIENT_ID_HERE` with your Client ID
9. Push the change to GitHub — Google Sign-In will now work!

## Features

- ✅ Works offline — all data saved locally in the browser
- ✅ Cash change calculator (no mental math needed)
- ✅ Expiry date alerts
- ✅ Low stock warnings
- ✅ Pending sync counter
- ✅ 20 South African shop types supported
- ✅ Google Sign-In (once configured)

## Store types supported

Spaza Shop, Tavern/Shebeen, Hair Salon, Barbershop, Tuck Shop, Butchery,
Fruit & Veg, Bakery, Takeaway/Braai Spot, Clothing & Alterations, Nail Salon,
Phone Repairs, Internet Café/Printing, Hardware Shop, Pharmacy, Muthi Shop,
Laundry, Mechanic/Tyre Shop, Creche/Daycare, Second-Hand/Recycling, Other
