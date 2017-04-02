library(ggplot2)
library(scales)
library(data.table)

all_dailies = read.csv("~/.fitbit/all-intraday.csv", head=F)

all_dailies$V1 = as.POSIXct(all_dailies$V1,format="%Y-%m-%d")

all_dailies$V2 = as.POSIXct(all_dailies$V2,format="%H:%M:%S")

all_dailies$V1 = months(all_dailies$V1)

all_dailies = data.table(all_dailies)[,list(V3=mean(V3)), by=c("V1","V2")]

all_dailies$V1 = factor(all_dailies$V1, levels =
                    c("January", "February", "March",
                      "April", "May", "June", "July",
                      "August", "September", "October",
                      "November", "December"))

plot = ggplot(all_dailies, aes(x = V2, y = V3, colour = V1)) +
  geom_line() +
  scale_colour_discrete(guide = FALSE) +
  scale_x_datetime(labels = date_format("%H:%M"),breaks = "1 hour")+
  xlab("time of day") + ylab("avg steps / min") +
  facet_grid(V1~.);

ggsave(filename = "/tmp/fitbit-months-plot.png",
       plot = plot,
       scale = 1,
       width = 12, height = 7, dpi = 100)

print(plot)