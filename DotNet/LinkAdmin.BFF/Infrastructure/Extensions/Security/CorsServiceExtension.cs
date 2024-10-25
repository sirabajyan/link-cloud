using LantanaGroup.Link.LinkAdmin.BFF.Application.Models.Configuration;
using Microsoft.AspNetCore.Cors.Infrastructure;

namespace LantanaGroup.Link.LinkAdmin.BFF.Infrastructure.Extensions.Security
{
    public static class CorsServiceExtension
    {
        public static IServiceCollection AddCorsService(this IServiceCollection services, Serilog.ILogger logger, Action<CorsServiceOptions>? options = null)
        {
            var corsServiceOptions = new CorsServiceOptions();
            options?.Invoke(corsServiceOptions);

            services.AddCors(options =>
            {
                CorsPolicyBuilder cpb = new();

                if (corsServiceOptions.AllowedOrigins?.Length > 0)
                {
                    cpb.WithOrigins(corsServiceOptions.AllowedOrigins);

                    if (corsServiceOptions.AllowCredentials)
                    {
                        cpb.AllowCredentials();
                        cpb.WithHeaders(corsServiceOptions.AllowedHeaders ?? corsServiceOptions.DefaultAllowedHeaders);
                    }
                }
                else
                {
                    cpb.SetIsOriginAllowed((Host) => true);

                    if (corsServiceOptions.AllowedHeaders?.Length > 0)
                    {
                        cpb.WithHeaders(corsServiceOptions.AllowedHeaders);
                    }
                    else
                    {
                        cpb.AllowAnyHeader();
                    }

                }

                cpb.WithMethods(corsServiceOptions.AllowedMethods is not null ? corsServiceOptions.AllowedMethods : corsServiceOptions.DefaultAllowedMethods);
                cpb.WithExposedHeaders(corsServiceOptions.AllowedExposedHeaders is not null ? corsServiceOptions.AllowedExposedHeaders : corsServiceOptions.DefaultAllowedExposedHeaders);
                cpb.SetPreflightMaxAge(TimeSpan.FromSeconds(corsServiceOptions.MaxAge));

                var policy = cpb.Build();
                logger.Information("Adding CORS policy {policyName} with config: {policy}", CorsConfig.DefaultCorsPolicyName, policy);
                options.AddPolicy(CorsConfig.DefaultCorsPolicyName, policy);

                //add health check endpoint to cors policy
                options.AddPolicy("HealthCheckPolicy", policy =>
                {
                    policy.AllowAnyHeader();
                    policy.AllowAnyMethod();
                    policy.AllowAnyOrigin();
                });
            });

            return services;
        }
    }

    public class CorsServiceOptions : CorsConfig
    {
        public IWebHostEnvironment Environment { get; set; } = null!;
    }
}
