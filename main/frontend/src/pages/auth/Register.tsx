import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Spinner } from "@/components/ui/spinner";
import { CreditCard, Eye, EyeOff } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { useI18n } from "@/context/I18nContext";
import { AuthNavbar } from "@/components/layout/AuthNavbar";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const registerSchema = z
  .object({
    firstName: z.string().min(2, "First name must be at least 2 characters"),
    middleName: z.string().optional(),
    lastName: z.string().min(2, "Last name must be at least 2 characters"),
    email: z.string().email("Please enter a valid email address"),
    password: z.string().min(6, "Password must be at least 6 characters"),
    confirmPassword: z.string(),
    countryCode: z
      .string()
      .regex(/^\+[0-9]{1,4}$/, "Country code must start with + and contain 1-4 digits"),
    phoneNumber: z
      .string()
      .regex(/^[0-9]{7,15}$/, "Phone number must contain 7-15 digits"),
    addressLine1: z.string().min(1, "Address line 1 is required").max(100, "Address line 1 cannot exceed 100 characters"),
    addressLine2: z.string().max(100, "Address line 2 cannot exceed 100 characters").optional(),
    street: z.string().min(1, "Street is required").max(100, "Street cannot exceed 100 characters"),
    city: z.string().min(1, "City is required").max(50, "City cannot exceed 50 characters"),
    state: z.string().min(1, "State is required").max(50, "State cannot exceed 50 characters"),
    pinCode: z.string().min(1, "Pin code is required").regex(/^[0-9]{4,10}$/, "Pin code must be 4-10 digits"),
    country: z.string().min(1, "Country is required").max(50, "Country cannot exceed 50 characters"),
    dateOfBirth: z.string().refine((date) => {
      const d = new Date(date);
      return d < new Date();
    }, "Date of birth must be in the past"),
    aadhaarNumber: z.string().regex(/^\d{12}$/, "Aadhaar must be 12 digits"),
    panNumber: z
      .string()
      .regex(/^[A-Z]{5}[0-9]{4}[A-Z]{1}$/, "Invalid PAN format"),
    preferredCurrency: z
      .string()
      .length(3, "Currency code must be 3 characters"),
    role: z.enum(["CUSTOMER", "BANKOFFICER", "ADMIN"]),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

type RegisterFormData = z.infer<typeof registerSchema>;

export function Register() {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { register } = useAuth();
  const { t } = useI18n();
  const navigate = useNavigate();

  const form = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      firstName: "",
      middleName: "",
      lastName: "",
      email: "",
      password: "",
      confirmPassword: "",
      countryCode: "",
      phoneNumber: "",
      addressLine1: "",
      addressLine2: "",
      street: "",
      city: "",
      state: "",
      pinCode: "",
      country: "",
      dateOfBirth: "",
      aadhaarNumber: "",
      panNumber: "",
      preferredCurrency: "KWD",
      role: "CUSTOMER",
    },
  });

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true);
    try {
      await register({
        firstName: data.firstName,
        middleName: data.middleName,
        lastName: data.lastName,
        email: data.email,
        password: data.password,
        countryCode: data.countryCode,
        phoneNumber: data.phoneNumber,
        line1: data.addressLine1,
        line2: data.addressLine2,
        street: data.street,
        city: data.city,
        state: data.state,
        pinCode: data.pinCode,
        country: data.country,
        dateOfBirth: data.dateOfBirth,
        aadhaarNumber: data.aadhaarNumber,
        panNumber: data.panNumber,
        preferredCurrency: data.preferredCurrency,
        role: data.role,
      });
      toast.success(t("auth.register.success"));
      navigate("/dashboard");
    } catch {
      toast.error(t("auth.register.failed"));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <AuthNavbar showAuthButtons={false} />
      <div className="min-h-screen flex items-center justify-center bg-linear-to-br from-primary/5 via-background to-secondary/5 p-4 pt-24">
        <Card className="w-full max-w-md">
          <CardHeader className="space-y-1 text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground">
              <CreditCard className="h-6 w-6" />
            </div>
            <CardTitle className="text-2xl font-bold">
              {t("auth.register.title")}
            </CardTitle>
            <CardDescription>{t("auth.register.subtitle")}</CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form
                onSubmit={form.handleSubmit(onSubmit)}
                className="space-y-4"
              >
                <div className="grid grid-cols-2 gap-4">
                  <FormField
                    control={form.control}
                    name="firstName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-firstName">
                          {t("auth.field.firstName")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-firstName"
                            placeholder={t("auth.placeholder.firstName")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="lastName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-lastName">
                          {t("auth.field.lastName")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-lastName"
                            placeholder={t("auth.placeholder.lastName")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                <FormField
                  control={form.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel htmlFor="register-email">
                        {t("auth.field.email")}
                      </FormLabel>
                      <FormControl>
                        <Input
                          id="register-email"
                          type="email"
                          placeholder={t("auth.placeholder.email")}
                          {...field}
                          disabled={isLoading}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <div className="grid grid-cols-3 gap-4">
                  <FormField
                    control={form.control}
                    name="countryCode"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-countryCode">
                          {t("auth.field.countryCode")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-countryCode"
                            placeholder={t("auth.placeholder.countryCode")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="phoneNumber"
                    render={({ field }) => (
                      <FormItem className="col-span-2">
                        <FormLabel htmlFor="register-phone">
                          {t("auth.field.phone")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-phone"
                            type="tel"
                            placeholder={t("auth.placeholder.phone")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                <FormField
                  control={form.control}
                  name="dateOfBirth"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel htmlFor="register-dateOfBirth">
                        {t("auth.field.dateOfBirth")}
                      </FormLabel>
                      <FormControl>
                        <Input
                          id="register-dateOfBirth"
                          type="date"
                          {...field}
                          disabled={isLoading}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="addressLine1"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel htmlFor="register-addressLine1">
                        {t("auth.field.addressLine1")}
                      </FormLabel>
                      <FormControl>
                        <Input
                          id="register-addressLine1"
                          placeholder={t("auth.placeholder.addressLine1")}
                          {...field}
                          disabled={isLoading}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="addressLine2"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel htmlFor="register-addressLine2">
                        {t("auth.field.addressLine2")}
                      </FormLabel>
                      <FormControl>
                        <Input
                          id="register-addressLine2"
                          placeholder={t("auth.placeholder.addressLine2")}
                          {...field}
                          disabled={isLoading}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <div className="grid grid-cols-2 gap-4">
                  <FormField
                    control={form.control}
                    name="street"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-street">
                          {t("auth.field.street")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-street"
                            placeholder={t("auth.placeholder.street")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="city"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-city">
                          {t("auth.field.city")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-city"
                            placeholder={t("auth.placeholder.city")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <FormField
                    control={form.control}
                    name="state"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-state">
                          {t("auth.field.state")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-state"
                            placeholder={t("auth.placeholder.state")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="pinCode"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-pinCode">
                          {t("auth.field.pinCode")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-pinCode"
                            placeholder={t("auth.placeholder.pinCode")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="country"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-country">
                          {t("auth.field.country")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-country"
                            placeholder={t("auth.placeholder.country")}
                            {...field}
                            disabled={isLoading}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <FormField
                    control={form.control}
                    name="aadhaarNumber"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-aadhaar">
                          {t("auth.field.aadhaarNumber")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-aadhaar"
                            placeholder={t("auth.placeholder.aadhaarNumber")}
                            {...field}
                            disabled={isLoading}
                            maxLength={12}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="panNumber"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel htmlFor="register-pan">
                          {t("auth.field.panNumber")}
                        </FormLabel>
                        <FormControl>
                          <Input
                            id="register-pan"
                            placeholder={t("auth.placeholder.panNumber")}
                            {...field}
                            disabled={isLoading}
                            maxLength={10}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                <FormField
                  control={form.control}
                  name="preferredCurrency"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>{t("auth.field.preferredCurrency")}</FormLabel>
                      <FormControl>
                        <Select
                          onValueChange={field.onChange}
                          defaultValue={field.value}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Select currency" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="KWD">
                              KWD - Kuwaiti Dinar
                            </SelectItem>
                            <SelectItem value="USD">USD - US Dollar</SelectItem>
                            <SelectItem value="EUR">EUR - Euro</SelectItem>
                            <SelectItem value="GBP">
                              GBP - British Pound
                            </SelectItem>
                            <SelectItem value="INR">
                              INR - Indian Rupee
                            </SelectItem>
                            <SelectItem value="SAR">
                              SAR - Saudi Riyal
                            </SelectItem>
                            <SelectItem value="AED">
                              AED - UAE Dirham
                            </SelectItem>
                          </SelectContent>
                        </Select>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="role"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>{t("auth.field.role")}</FormLabel>
                      <FormControl>
                        <Select
                          onValueChange={field.onChange}
                          defaultValue={field.value}
                        >
                          <SelectTrigger>
                            <SelectValue
                              placeholder={t("auth.placeholder.role")}
                            />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="CUSTOMER">
                              {t("role.CUSTOMER")}
                            </SelectItem>
                            <SelectItem value="BANKOFFICER">
                              {t("role.BANKOFFICER")}
                            </SelectItem>
                            <SelectItem value="ADMIN">
                              {t("role.ADMIN")}
                            </SelectItem>
                          </SelectContent>
                        </Select>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="password"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel htmlFor="register-password">
                        {t("auth.field.password")}
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <Input
                            id="register-password"
                            type={showPassword ? "text" : "password"}
                            placeholder={t("auth.placeholder.password")}
                            {...field}
                            disabled={isLoading}
                          />
                          <Button
                            type="button"
                            variant="ghost"
                            size="sm"
                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                            onClick={() => setShowPassword(!showPassword)}
                            disabled={isLoading}
                            aria-label={
                              showPassword
                                ? t("auth.hidePassword")
                                : t("auth.showPassword")
                            }
                          >
                            {showPassword ? (
                              <EyeOff className="h-4 w-4" />
                            ) : (
                              <Eye className="h-4 w-4" />
                            )}
                          </Button>
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="confirmPassword"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel htmlFor="register-confirmPassword">
                        {t("auth.field.confirmPassword")}
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <Input
                            id="register-confirmPassword"
                            type={showConfirmPassword ? "text" : "password"}
                            placeholder={t("auth.placeholder.confirmPassword")}
                            {...field}
                            disabled={isLoading}
                          />
                          <Button
                            type="button"
                            variant="ghost"
                            size="sm"
                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                            onClick={() =>
                              setShowConfirmPassword(!showConfirmPassword)
                            }
                            disabled={isLoading}
                            aria-label={
                              showConfirmPassword
                                ? t("auth.hidePassword")
                                : t("auth.showPassword")
                            }
                          >
                            {showConfirmPassword ? (
                              <EyeOff className="h-4 w-4" />
                            ) : (
                              <Eye className="h-4 w-4" />
                            )}
                          </Button>
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <Button type="submit" className="w-full" disabled={isLoading}>
                  {isLoading ? (
                    <>
                      <Spinner className="h-4 w-4 animate-spin" />
                      {t("auth.register.creating")}
                    </>
                  ) : (
                    t("auth.register.submit")
                  )}
                </Button>
              </form>
            </Form>
            <div className="mt-6 text-center text-sm">
              {t("auth.register.haveAccount")}{" "}
              <Link
                to="/login"
                className="font-medium text-primary hover:underline"
              >
                {t("auth.register.signIn")}
              </Link>
            </div>
          </CardContent>
        </Card>
      </div>
    </>
  );
}
