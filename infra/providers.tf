terraform {
  cloud {
    organization = "TickleThePanda"
    workspaces {
      name = "health-vis"
    }
  }
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
    vercel = {
      source  = "vercel/vercel"
      version = "~> 0.8.0"
    }
  }
}

provider "aws" {
  default_tags {
    tags = {
      TerraformManaged = "True"
    }
  }
  region = "us-east-1"
}


