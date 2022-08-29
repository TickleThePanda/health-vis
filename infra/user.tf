
resource "aws_iam_user" "HealthVis" {
  name = "HealthVis"
}

resource "aws_iam_access_key" "HealthVis" {
  user = aws_iam_user.HealthVis.name
}

data "aws_iam_policy_document" "HealthVis" {
  statement {
    actions = [
      "dynamodb:BatchGetItem",
      "dynamodb:BatchWriteItem",
      "dynamodb:PutItem",
      "dynamodb:DescribeTable",
      "dynamodb:DeleteItem",
      "dynamodb:GetItem",
      "dynamodb:Scan",
      "dynamodb:Query",
      "dynamodb:UpdateItem",
      "dynamodb:PartiQLSelect"
    ]

    resources = [
      aws_dynamodb_table.health-vis.arn,
    ]
  }
}

resource "aws_iam_policy" "HealthVis" {
  name   = "health_vis"
  policy = data.aws_iam_policy_document.HealthVis.json
}

resource "aws_iam_user_policy_attachment" "HealthVis" {
  user       = aws_iam_user.HealthVis.name
  policy_arn = aws_iam_policy.HealthVis.arn
}
