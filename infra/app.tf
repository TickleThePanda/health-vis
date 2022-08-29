
variable "auth_secret" {
  sensitive = true
}

resource "vercel_project" "health-vis" {
  name = "health-vis"

  git_repository = {
    type = "github"
    repo = "TickleThePanda/health-vis"
  }

  environment = [
    {
      key    = "HEALTH_APP_SECRET_KEY"
      target = ["production", "preview", "development"]
      value  = var.auth_secret
    },
    {
      key    = "HEALTH_VIS_AWS_ACCESS_KEY_ID"
      target = ["production", "preview", "development"]
      value  = aws_iam_access_key.HealthVis.id
    },
    {
      key    = "HEALTH_VIS_AWS_SECRET_ACCESS_KEY"
      target = ["production", "preview", "development"]
      value  = aws_iam_access_key.HealthVis.secret
    }
  ]

  serverless_function_region = "iad1"
}
