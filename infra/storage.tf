resource "aws_dynamodb_table" "health-vis" {
  name           = "Weight"
  billing_mode   = "PROVISIONED"
  read_capacity  = "5"
  write_capacity = "5"
  stream_enabled = false

  hash_key  = "YearMonth"
  range_key = "DayPeriod"

  attribute {
    name = "YearMonth"
    type = "S"
  }

  attribute {
    name = "DayPeriod"
    type = "S"
  }
}

