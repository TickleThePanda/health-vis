#!/usr/bin/env Rscript
library(RCurl)
library(ggplot2)

spreadsheet <- getURL("https://docs.google.com/spreadsheets/d/16LpbP9Uu0lPS-_3WfPosIvb9YJIHzUK6RolntThbJrk/export?gid=1114727413&format=csv")
data <- read.csv(textConnection(spreadsheet), head = T, na.strings=c("-"))

diff_am_pm = data$Weight.PM - data$Weight.AM

avg_diff_am_pm = mean(diff_am_pm[complete.cases(diff_am_pm)])

data$Weight.AM[is.na(data$Weight.AM)] = data$Weight.PM[is.na(data$Weight.AM)] - avg_diff_am_pm
data$Weight.PM[is.na(data$Weight.PM)] = data$Weight.AM[is.na(data$Weight.PM)] + avg_diff_am_pm
data$Weight.Mean = rowMeans(subset(data, select = c(Weight.AM, Weight.AM)))

data$Date <- as.Date(data$Date , "%d/%m/%Y");

complete_means = data$Weight.Mean[complete.cases(data$Weight.Mean)]

current_weight = round(mean(tail(complete_means, 7)), 1)

plot = ggplot(data, aes(x = Date, y = Weight.Mean), na.rm = T) +
  geom_line(colour = "#56B4E9", na.rm = T) +
  stat_smooth(method = "loess",
              formula = y ~ x,
              size = 1,
              na.rm = T,
              fullrange = T,
              se = FALSE,
              colour = "#D55E00") +
  xlab("date")+
  ylab("weight (kg)");

write(current_weight, file="/tmp/current-weight")

ggsave(filename = "/tmp/weight-plot.svg",
       plot = plot,
       scale = 1,
       width = 12, height = 7, dpi = 100)
