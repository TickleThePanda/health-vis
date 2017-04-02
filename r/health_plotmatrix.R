#!/usr/bin/env Rscript
library(RCurl)
library(ggplot2)
library(GGally)

spreadsheet <- getURL("https://docs.google.com/spreadsheets/d/16LpbP9Uu0lPS-_3WfPosIvb9YJIHzUK6RolntThbJrk/export?gid=1114727413&format=csv")
data <- read.csv(textConnection(spreadsheet), head = T, na.strings=c("-"))

diff_am_pm = data$Weight.PM - data$Weight.AM

avg_diff_am_pm = mean(diff_am_pm[complete.cases(diff_am_pm)])

data$Weight.AM[is.na(data$Weight.AM)] = data$Weight.PM[is.na(data$Weight.AM)] - avg_diff_am_pm
data$Weight.PM[is.na(data$Weight.PM)] = data$Weight.AM[is.na(data$Weight.PM)] + avg_diff_am_pm
data$Weight.Mean = rowMeans(subset(data, select = c(Weight.AM, Weight.AM)))

data$Date <- as.Date(data$Date, "%d/%m/%Y");

plot = qplot(data = data, y = Mood, x = Alcohol.Units)
print(plot)
# 
# ggsave(filename = "/tmp/weight/weight_plot.png",
#        plot = plot,
#        scale = 1,
#        width = 12, height = 7, dpi = 100)