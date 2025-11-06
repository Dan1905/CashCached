# Frontend-Backend Alignment Summary

## Date: November 5, 2025

This document outlines the frontend changes made to align with the refactored customer backend service.

## Backend Changes (Completed)
1. ✅ Refactored MagicLinkService to throw `MagicLinkException` instead of `InvalidCredentialsException`
2. ✅ Updated all test cases to expect correct exception types
3. ✅ Structured DTOs for registration (Name, MobileNumber, Address)
4. ✅ Consistent response format using `AuthResponse` DTO

## Frontend Changes (Completed)

### 1. AuthContext.tsx (`/main/frontend/src/context/AuthContext.tsx`)

**Updated `RegisterData` Interface:**
- Changed from flat structure to nested structure matching backend DTOs
- Added fields:
  - `middleName?: string` - Optional middle name
  - `countryCode: string` - Country code for mobile (e.g., +965)
  - `addressLine1`, `addressLine2`, `street`, `city`, `state`, `pinCode` - Structured address fields
- Removed `address?: string` (replaced with structured fields)
- Removed `phoneNumber: string` as single field (now split into countryCode + phoneNumber)

**Updated `register()` Function:**
```typescript
// OLD: Flat payload
{
  fullName: `${firstName} ${lastName}`,
  phoneNumber: "...",
  address: "..."
}

// NEW: Structured payload matching backend DTOs
{
  name: {
    firstName: "...",
    middleName: "..." || null,
    lastName: "..."
  },
  mobileNumber: {
    countryCode: "+965",
    number: "12345678"
  },
  address: {
    line1: "...",
    line2: "..." || null,
    street: "...",
    city: "...",
    state: "...",
    pinCode: "..."
  }
}
```

### 2. Register.tsx (`/main/frontend/src/pages/auth/Register.tsx`)

**Updated Registration Schema:**
- Added `middleName?: string` field
- Split phone into:
  - `countryCode` with validation: `/^\+[0-9]{1,4}$/`
  - `phoneNumber` with validation: `/^[0-9]{7,15}$/`
- Replaced single `address` field with:
  - `addressLine1` (max 100 chars, optional)
  - `addressLine2` (max 100 chars, optional)
  - `street` (max 100 chars, optional)
  - `city` (max 50 chars, optional)
  - `state` (max 50 chars, optional)
  - `pinCode` (4-10 digits, optional)

**Updated Form Fields:**
- Added country code field before phone number (grid layout)
- Added 6 new address fields replacing single address textarea
- Default country code: "+965" (Kuwait)

**Form Layout Structure:**
```
[First Name] [Last Name]
[Email]
[Country Code] [Phone Number]    <- Split into 2 fields
[Date of Birth]
[Address Line 1]                 <- New structured fields
[Address Line 2]
[Street] [City]
[State] [Pin Code]
[Aadhaar] [PAN]
[Preferred Currency]
[Role]
[Password] [Confirm Password]
```

## Backend API Endpoints (No Changes Required)

The following endpoints already support the new structure:
- `POST /api/auth/register` - Accepts RegisterRequest DTO
- `POST /api/auth/login` - Returns AuthResponse DTO
- `POST /api/auth/magic-link/request` - No changes
- `POST /api/auth/magic-link/verify` - Returns AuthResponse DTO

## Response Structure (Consistent)

All authentication endpoints now return:
```json
{
  "token": "session-id",
  "email": "user@example.com",
  "role": "CUSTOMER",
  "message": "Success message",
  "tokenType": "Bearer",
  "twoFactorRequired": false
}
```

## Exception Handling

**Backend Exceptions:**
- `MagicLinkException` - Returns 400 status for magic link errors
- `InvalidCredentialsException` - Returns 400 status for login errors
- `UserAlreadyExistsException` - Returns 409 status for duplicate users

**Frontend Error Handling:**
- Already handles 400 status codes correctly
- Error messages extracted from `response.data.message`
- No changes needed to error handling logic

## Testing Status

### Backend Tests: ✅ ALL PASSING (60/60)
- MagicLinkServiceTest: 7/7 ✅
- AuthControllerTest: 6/6 ✅
- CustomerControllerTest: 6/6 ✅
- All other service tests: 41/41 ✅

### Frontend Tests:
- Manual testing required for registration flow
- Verify magic link flow still works
- Test error handling for various scenarios

## Migration Checklist

- [x] Update AuthContext RegisterData interface
- [x] Update register() function payload structure
- [x] Update Register.tsx schema validation
- [x] Add new form fields in Register.tsx
- [x] Update default values
- [x] Verify country code validation
- [x] Verify address field validations
- [ ] Test complete registration flow
- [ ] Test magic link authentication
- [ ] Test error scenarios
- [ ] Update any API documentation

## Breaking Changes

**For Frontend Developers:**
⚠️ If you have any custom registration forms, update them to use the new structure:
1. Split `phoneNumber` into `countryCode` + `phoneNumber`
2. Replace `address` string with structured address object
3. Add optional `middleName` field

**For Backend Developers:**
✅ No breaking changes - backend properly handles both old and new formats via DTOs

## Notes

- All styling/CSS remains unchanged ✅
- No color scheme modifications ✅
- No layout design changes (only field additions) ✅
- Backward compatible: Old mobile apps may need updates
- Address fields are optional but recommended for complete profiles

## Next Steps

1. Start the customer service: `cd customer && ./mvnw spring-boot:run`
2. Start the frontend: `cd main/frontend && npm run dev`
3. Test registration with new fields
4. Verify magic link authentication
5. Check error handling scenarios
