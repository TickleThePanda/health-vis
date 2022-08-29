import { Request, Response, NextFunction } from "express";

interface RequestWithUser extends Request {
  auth: {
    roles: string[];
  };
}

export function requireAdmin() {
  return (req: RequestWithUser, res: Response, next: NextFunction) => {
    if (!req.auth.roles.includes("admin")) {
      res.send(401);
    } else {
      next();
    }
  };
}
