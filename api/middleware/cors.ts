import cors from "cors";

const whitelist = [
  /^https?:\/\/(((\w|-)*\.)*ticklethepanda\.(co\.uk|dev|netlify\.com)|localhost:?[0-9]*)$/,
];

export const corsOptions: cors.CorsOptions = {
  maxAge: 86400,
  origin: function (origin, callback) {
    if (!origin) {
      callback(null, true);
    } else if (whitelist.some((r) => origin.match(r))) {
      callback(null, true);
    } else {
      callback(new Error("Not allowed by CORS"));
    }
  },
  credentials: true,
};

export function appCors() {
  return cors(corsOptions);
}
