using Confluent.Kafka;
using LantanaGroup.Link.LinkAdmin.BFF.Application.Models.Integration;
using LantanaGroup.Link.LinkAdmin.BFF.Infrastructure;
using LantanaGroup.Link.LinkAdmin.BFF.Infrastructure.Logging;
using LantanaGroup.Link.Shared.Application.Models;
using OpenTelemetry.Trace;
using System.Diagnostics;
using System.Text.Json;

namespace LantanaGroup.Link.LinkAdmin.BFF.Application.Commands.Integration
{
    public class CreateReportScheduled : ICreateReportScheduled
    {
        private readonly ILogger<CreateReportScheduled> _logger;
        private readonly IProducer<string, object> _producer;

        public CreateReportScheduled(ILogger<CreateReportScheduled> logger, IProducer<string, object> producer)
        {
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
            _producer = producer ?? throw new ArgumentNullException(nameof(producer));
        }

        public async Task<string> Execute(ReportScheduled model, string? userId = null)
        {
            using Activity? activity = ServiceActivitySource.Instance.StartActivity("Producing Report Scheduled Event");
            string correlationId = Guid.NewGuid().ToString();

            try
            {
                var headers = new Headers
                {
                    { "X-Correlation-Id", System.Text.Encoding.ASCII.GetBytes(correlationId) }
                };

                string Key = model.FacilityId;
                DateTime EndDate = DateTime.UtcNow.AddMinutes(2);
                DateTime EndDate1 = new DateTime(EndDate.Year, EndDate.Month, EndDate.Day, EndDate.Hour, EndDate.Minute, 0, DateTimeKind.Utc);
                var message = new Message<string, object>
                {
                    Key = model.FacilityId,
                    Headers = headers,
                    Value = new ReportScheduledMessage()
                    {
                        ReportTypes = model.ReportTypes,
                        Frequency = "Monthly",
                        StartDate = model.StartDate,
                        // calculate end date based on UTC time now plus 2 minutes
                        EndDate = EndDate1,

                    },
                };

                await _producer.ProduceAsync(nameof(KafkaTopic.ReportScheduled), message);
                _logger.LogKafkaProducerReportScheduled(correlationId);

                return correlationId;

            }
            catch (Exception ex)
            {
                Activity.Current?.SetStatus(ActivityStatusCode.Error);
                Activity.Current?.RecordException(ex);
                _logger.LogKafkaProducerException(correlationId, ex.Message);
                throw;
            }

        }
    }
}
